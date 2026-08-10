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
package org.codelibs.fess.chunk;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ChunkBoundaryFinderTest extends UnitFessTestCase {

    private ExposedFinder finder;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        finder = new ExposedFinder();
    }

    @Test
    public void test_isBreakableSpace_coversUnicodeSpacesJavaMisses() {
        // Character.isWhitespace() returns false for these, but they are legitimate
        // break opportunities in extracted text.
        assertTrue(finder.breakableSpace(' '));
        assertTrue(finder.breakableSpace('\t'));
        assertTrue(finder.breakableSpace('\n'));
        assertTrue(finder.breakableSpace(0x3000)); // IDEOGRAPHIC SPACE
        assertTrue(finder.breakableSpace(0x00A0)); // NBSP
        assertTrue(finder.breakableSpace(0x2007)); // FIGURE SPACE
        assertTrue(finder.breakableSpace(0x202F)); // NNBSP
        assertTrue(finder.breakableSpace(0x200B)); // ZWSP
        assertTrue(finder.breakableSpace(0x200C)); // ZWNJ
        assertTrue(finder.breakableSpace(0x2060)); // WORD JOINER
        assertTrue(finder.breakableSpace(0xFEFF)); // ZWNBSP / BOM
        assertTrue(finder.breakableSpace(0x0085)); // NEL
        assertFalse(finder.breakableSpace('a'));
        assertFalse(finder.breakableSpace(0x3042)); // HIRAGANA A
        assertFalse(finder.breakableSpace(0x200D)); // ZWJ is a joiner, not a break
    }

    @Test
    public void test_isNewline_coversUnicodeLineSeparators() {
        assertTrue(finder.newline('\n'));
        assertTrue(finder.newline('\r'));
        assertTrue(finder.newline(0x0085)); // NEL
        assertTrue(finder.newline(0x2028)); // LINE SEPARATOR
        assertTrue(finder.newline(0x2029)); // PARAGRAPH SEPARATOR
        assertFalse(finder.newline(' '));
        assertFalse(finder.newline('\t'));
    }

    @Test
    public void test_isSentenceTerminator_japaneseAndAscii() {
        assertTrue(finder.sentenceTerminator(0x3002)); // 。
        assertTrue(finder.sentenceTerminator(0xFF0E)); // ．
        assertTrue(finder.sentenceTerminator(0xFF61)); // ｡
        assertTrue(finder.sentenceTerminator(0xFF01)); // ！
        assertTrue(finder.sentenceTerminator(0xFF1F)); // ？
        assertTrue(finder.sentenceTerminator('!'));
        assertTrue(finder.sentenceTerminator('?'));
        assertTrue(finder.sentenceTerminator('.'));
        assertTrue(finder.sentenceTerminator(0x2026)); // …
        assertTrue(finder.sentenceTerminator(0x06D4)); // Arabic full stop
        assertTrue(finder.sentenceTerminator(0x2E3C)); // stenographic full stop
        assertFalse(finder.sentenceTerminator(0x3001)); // 、 is a clause separator
        assertFalse(finder.sentenceTerminator('a'));
    }

    @Test
    public void test_isClauseSeparator_japaneseAndAsciiAndDashes() {
        assertTrue(finder.clauseSeparator(0x3001)); // 、
        assertTrue(finder.clauseSeparator(0xFF0C)); // ，
        assertTrue(finder.clauseSeparator(0xFF1B)); // ；
        assertTrue(finder.clauseSeparator(0xFF1A)); // ：
        assertTrue(finder.clauseSeparator(0x30FB)); // ・
        assertTrue(finder.clauseSeparator(','));
        assertTrue(finder.clauseSeparator(';'));
        assertTrue(finder.clauseSeparator(':'));
        assertTrue(finder.clauseSeparator('-'));
        assertTrue(finder.clauseSeparator(0x2014)); // em dash
        assertTrue(finder.clauseSeparator(0x301C)); // 〜
        assertFalse(finder.clauseSeparator(0x3002)); // 。 is a sentence terminator
        assertFalse(finder.clauseSeparator('a'));
    }

    @Test
    public void test_isAsciiPunctuationRequiringSpace_onlyAmbiguousAsciiMarks() {
        // These are ambiguous inside numbers/abbreviations (3.14, 1,234), so they only
        // count as boundaries when whitespace follows.
        assertTrue(finder.asciiRequiringSpace('.'));
        assertTrue(finder.asciiRequiringSpace(','));
        assertTrue(finder.asciiRequiringSpace(';'));
        assertTrue(finder.asciiRequiringSpace(':'));
        assertFalse(finder.asciiRequiringSpace('!'));
        assertFalse(finder.asciiRequiringSpace('-'));
        assertFalse(finder.asciiRequiringSpace(0x3002)); // 。 never needs a following space
        assertFalse(finder.asciiRequiringSpace(0x3001)); // 、 never needs a following space
    }

    @Test
    public void test_brackets() {
        assertTrue(finder.closingBracket(0xFF09)); // ）
        assertTrue(finder.closingBracket(0x300D)); // 」
        assertTrue(finder.closingBracket(')'));
        assertTrue(finder.closingBracket('"'));
        assertTrue(finder.openingBracket(0xFF08)); // （
        assertTrue(finder.openingBracket(0x300C)); // 「
        assertTrue(finder.openingBracket('('));
        assertFalse(finder.closingBracket('('));
        assertFalse(finder.openingBracket(')'));
    }

    @Test
    public void test_isSkippableAfterBoundary_spacesAndClosingMarks() {
        // The run that gets swallowed into the preceding chunk.
        assertTrue(finder.skippable(' '));
        assertTrue(finder.skippable(0x0085)); // NEL
        assertTrue(finder.skippable(0x300D)); // 」
        assertTrue(finder.skippable(')'));
        assertFalse(finder.skippable('a'));
        assertFalse(finder.skippable(0x3002)); // the terminator itself is not skippable
        assertFalse(finder.skippable(0x300C)); // an opening bracket is not skippable
    }

    @Test
    public void test_isScriptBoundary_ignoresCommonAndInherited() {
        assertTrue(finder.scriptBoundary(0x90FD, 'T')); // 都 (HAN) -> T (LATIN)
        assertTrue(finder.scriptBoundary(0x6F22, 0x3042)); // 漢 (HAN) -> あ (HIRAGANA)
        assertTrue(finder.scriptBoundary(0x3042, 0x30A2)); // あ (HIRAGANA) -> ア (KATAKANA)
        assertFalse(finder.scriptBoundary('a', 'b'));
        assertFalse(finder.scriptBoundary('a', '1')); // digits are COMMON
        assertFalse(finder.scriptBoundary('1', 'a'));
        assertFalse(finder.scriptBoundary(0x3042, 0x1F600)); // emoji are COMMON
    }

    @Test
    public void test_isClusterContinuation_marksAndJoiners() {
        assertTrue(finder.clusterContinuation(0x3099)); // COMBINING KATAKANA-HIRAGANA VOICED SOUND MARK
        assertTrue(finder.clusterContinuation(0x0301)); // COMBINING ACUTE ACCENT
        assertTrue(finder.clusterContinuation(0xFE0F)); // VARIATION SELECTOR-16
        assertTrue(finder.clusterContinuation(0xE0101)); // VARIATION SELECTOR-18
        assertTrue(finder.clusterContinuation(0x200D)); // ZWJ
        assertFalse(finder.clusterContinuation('a'));
        assertFalse(finder.clusterContinuation(0x3042));
    }

    // Literal pin: the component name is the seam a deployment overrides to swap the boundary
    // rules. Losing the definition would silently fall back to the built-in finder.
    @Test
    public void test_fessChunkXml_definesChunkBoundaryFinderComponent() throws Exception {
        final String xml;
        try (var in = getClass().getClassLoader().getResourceAsStream("fess_chunk.xml")) {
            assertTrue(in != null, "fess_chunk.xml must be on the classpath");
            xml = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
        assertTrue(xml.contains("name=\"chunkBoundaryFinder\""), "fess_chunk.xml must define the chunkBoundaryFinder component");
        assertTrue(xml.contains("class=\"org.codelibs.fess.chunk.ChunkBoundaryFinder\""),
                "chunkBoundaryFinder must point at ChunkBoundaryFinder");
    }

    // ===================================================================================
    //                                                                     backward search
    //                                                                     ===============

    @Test
    public void test_findChunkEnd_zeroWindows_returnsIdealUnchanged() {
        final String text = "abc def ghi";
        assertEquals(5, finder.findChunkEnd(text, 0, 5, text.length(), 0, 0));
    }

    @Test
    public void test_findChunkEnd_idealAtEnd_returnsEndWithoutSearching() {
        // The final chunk must not be cut short just because a boundary sits nearby.
        final String text = "abc def";
        assertEquals(text.length(), finder.findChunkEnd(text, 0, text.length(), text.length(), 4, 4));
    }

    @Test
    public void test_findChunkEnd_strong_afterAsciiPeriodFollowedBySpace() {
        //            0123456789012345
        final String text = "one two. three four";
        // ideal=12 is inside "three"; the nearest strong boundary is just after "two. ".
        assertEquals(9, finder.findChunkEnd(text, 0, 12, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_strong_afterJapaneseFullStop() {
        final String text = "これは本文です。次の文がここから始まります";
        final int ideal = 12; // inside "次の文が"
        assertEquals(8, finder.findChunkEnd(text, 0, ideal, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_strong_swallowsClosingBracketAndSpaceRun() {
        final String text = "彼は「そうだ。」 と答えた。";
        // The boundary after 。」 plus the trailing space is index 9.
        assertEquals(9, finder.findChunkEnd(text, 0, 12, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_strong_afterNewlineRun() {
        final String text = "first line\n\nsecond paragraph";
        assertEquals(12, finder.findChunkEnd(text, 0, 16, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_asciiPeriodWithoutFollowingSpace_isNotStrong() {
        // "3.14" must not be treated as a sentence end; the space boundary wins instead.
        final String text = "value 3.14159 rest";
        assertEquals(6, finder.findChunkEnd(text, 0, 10, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_asciiCommaInsideNumber_isNotWeak() {
        // "1,234" must not break at the comma; the preceding space boundary is used.
        final String text = "total 1,234567 yen";
        assertEquals(6, finder.findChunkEnd(text, 0, 11, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_weak_afterJapaneseComma() {
        final String text = "まず準備をして、それから実行します";
        assertEquals(8, finder.findChunkEnd(text, 0, 11, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_weak_beforeOpeningBracket() {
        final String text = "説明文はここまで（補足はこちら）";
        assertEquals(8, finder.findChunkEnd(text, 0, 11, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_weak_atSpaceRun() {
        final String text = "alpha beta gamma delta";
        // ideal=13 is inside "gamma"; the nearest space boundary is right after "beta ".
        assertEquals(11, finder.findChunkEnd(text, 0, 13, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_strongBeatsNearerWeak() {
        // A weak boundary sits closer to ideal, but the strong one must win.
        final String text = "文の終わり。ここは、途中です";
        // 。 boundary = 6, 、 boundary = 10, ideal = 12
        assertEquals(6, finder.findChunkEnd(text, 0, 12, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_weakBeatsScript() {
        // Both a comma boundary and a script change are in the window; weak wins.
        final String text = "データを集計、abcdefgh";
        assertEquals(7, finder.findChunkEnd(text, 0, 11, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_script_whenNothingElseAvailable() {
        final String text = "東京都Tokyoにあります";
        // ideal=6 is inside "Tokyo"; the HAN->LATIN change at index 3 is the only candidate.
        assertEquals(3, finder.findChunkEnd(text, 0, 6, text.length(), 4, 0));
    }

    @Test
    public void test_findChunkEnd_noCandidate_returnsIdeal() {
        final String text = "あ".repeat(50);
        assertEquals(20, finder.findChunkEnd(text, 0, 20, text.length(), 5, 0));
    }

    @Test
    public void test_findChunkEnd_candidateOutsideWindow_isIgnored() {
        // The 。 is 10 chars before ideal but the window only reaches 3 back.
        final String text = "終わり。あいうえおかきくけこさしすせそ";
        assertEquals(14, finder.findChunkEnd(text, 0, 14, text.length(), 3, 0));
    }

    @Test
    public void test_findChunkEnd_neverReturnsAtOrBeforeStart() {
        // The only boundary in the text is a space sitting immediately before `start`; a window
        // wide enough to otherwise reach back to it must still be clamped so the search never
        // returns `start` itself (no forward progress) or before it.
        final String text = " bbbbbbbbbb";
        final int start = 1;
        final int result = finder.findChunkEnd(text, start, 8, text.length(), 20, 0);
        assertTrue(result > start, "must make forward progress, got " + result);
        assertTrue(result <= text.length(), "must not exceed limitEnd, got " + result);
    }

    @Test
    public void test_findChunkEnd_neverLandsInsideSurrogatePair() {
        final String text = "あ😀".repeat(20);
        for (int ideal = 2; ideal < text.length(); ideal++) {
            final int result = finder.findChunkEnd(text, 0, ideal, text.length(), 4, 0);
            assertFalse(result > 0 && result < text.length() && Character.isHighSurrogate(text.charAt(result - 1))
                    && Character.isLowSurrogate(text.charAt(result)), "boundary split a surrogate pair at " + result);
        }
    }

    @Test
    public void test_findChunkEnd_runReachingWindowFloor_isStillACandidate() {
        // Regression: the window ending in the middle of a skippable run used to discard the
        // run's boundary entirely, cutting mid-word even though the boundary was inside the window.
        final String text = "first\n\nsecond";
        assertEquals(7, finder.findChunkEnd(text, 0, 9, text.length(), 3, 0));
        assertEquals(7, finder.findChunkEnd(text, 0, 9, text.length(), 4, 0));
    }

    @Test
    public void test_findChunkEnd_weakRunReachingWindowFloor_isStillACandidate() {
        final String text = "alpha beta gamma";
        assertEquals(11, finder.findChunkEnd(text, 0, 13, text.length(), 2, 0));
        assertEquals(11, finder.findChunkEnd(text, 0, 13, text.length(), 3, 0));
    }

    @Test
    public void test_findChunkEnd_lookbackIsMonotonic() {
        // Growing the window must never turn a good boundary back into a worse one.
        final String text = "alpha beta gamma delta epsilon";
        int previous = -1;
        for (int lookback = 1; lookback <= 12; lookback++) {
            final int result = finder.findChunkEnd(text, 0, 20, text.length(), lookback, 0);
            assertTrue(result > 0 && result <= text.length(), "lookback=" + lookback + " result=" + result);
            if (previous != -1) {
                assertTrue(result <= previous, "widening lookback must not move the boundary later: lookback=" + lookback);
            }
            previous = result;
        }
    }

    @Test
    public void test_findChunkEnd_idealJustPastStartInsideSurrogatePair_escapesForward() {
        // Pulling back would land on start and leave no forward progress, so the offset must
        // move past the pair instead of splitting it.
        final String text = "😀ab";
        final int result = finder.findChunkEnd(text, 0, 1, text.length(), 4, 0);
        assertEquals(2, result);
        assertFalse(Character.isLowSurrogate(text.charAt(result)) && Character.isHighSurrogate(text.charAt(result - 1)),
                "boundary must not split a surrogate pair");
    }

    @Test
    public void test_findChunkEnd_nearestWeakWinsWithinTheTier() {
        // Two weak candidates in the window (11 and 6); the one nearest the ideal offset wins.
        final String text = "alpha beta gamma delta";
        assertEquals(11, finder.findChunkEnd(text, 0, 13, text.length(), 10, 0));
    }

    @Test
    public void test_findChunkEnd_nearestScriptChangeWinsWithinTheTier() {
        // Two script changes in the window (HAN->LATIN at 3, LATIN->HAN at 8) and no weak or
        // strong candidate anywhere; the one nearest the ideal offset must win.
        final String text = "東京都Tokyo日本語です";
        assertEquals(8, finder.findChunkEnd(text, 0, 10, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_oversizedLimitEnd_doesNotThrow() {
        // limitEnd is contractually the content length, but an oversized bound must degrade to a
        // clamped answer rather than an IndexOutOfBounds from a public component method.
        final String text = "ab ";
        final int result = finder.findChunkEnd(text, 0, 3, 10, 2, 0);
        assertTrue(result > 0 && result <= text.length(), "result must stay within the text, got " + result);
    }

    // ===================================================================================
    //                                                     forward search & cluster adjust
    //                                                     ===============================

    @Test
    public void test_findChunkEnd_forward_takesSentenceEndJustAhead() {
        // No strong boundary behind ideal, but "。" sits 2 chars ahead: overshoot to keep the
        // sentence whole.
        final String text = "あいうえおかきくけこ。さしすせそ";
        assertEquals(11, finder.findChunkEnd(text, 0, 9, text.length(), 3, 4));
    }

    @Test
    public void test_findChunkEnd_forward_swallowsTrailingRunWithinCeiling() {
        final String text = "abcdefgh. ijklmnop";
        // ideal=7, terminator at 8, trailing space at 9 -> boundary 10.
        assertEquals(10, finder.findChunkEnd(text, 0, 7, text.length(), 0, 5));
    }

    @Test
    public void test_findChunkEnd_forward_notTakenWhenBackwardStrongExists() {
        // A "。" exists both behind and ahead; the backward one must win (never overshoot
        // when the chunk size can be honoured).
        final String text = "まず終わり。あいうえお。つづき";
        assertEquals(6, finder.findChunkEnd(text, 0, 9, text.length(), 6, 6));
    }

    @Test
    public void test_findChunkEnd_forward_beyondLookahead_isIgnored() {
        final String text = "あいうえおかきくけこさしすせそ。つづき";
        // The 。 is 6 chars past ideal but lookahead only reaches 2; fall back to hard cut.
        assertEquals(9, finder.findChunkEnd(text, 0, 9, text.length(), 0, 2));
    }

    @Test
    public void test_findChunkEnd_forward_asciiPeriodWithoutSpace_isIgnored() {
        final String text = "abcdefg3.14159xyz";
        assertEquals(7, finder.findChunkEnd(text, 0, 7, text.length(), 0, 4));
    }

    @Test
    public void test_findChunkEnd_forward_preferredOverBackwardWeak() {
        // Backward has only a weak boundary; a strong one is just ahead and wins.
        final String text = "リスト、あいう。つづき";
        assertEquals(8, finder.findChunkEnd(text, 0, 6, text.length(), 6, 3));
    }

    @Test
    public void test_findChunkEnd_hardCut_doesNotSplitCombiningMark() {
        // KA + COMBINING VOICED SOUND MARK ("ga"). A cut in front of U+3099 orphans the mark.
        final String text = "\u304B\u3099".repeat(20);
        final int result = finder.findChunkEnd(text, 0, 5, text.length(), 0, 0);
        assertFalse(finder.clusterContinuation(text.codePointAt(result)), "boundary must not sit in front of a combining mark");
    }

    @Test
    public void test_findChunkEnd_hardCut_doesNotSplitZwjEmojiSequence() {
        // Family emoji: person ZWJ person ZWJ person. Written with Unicode escapes on purpose --
        // a literal zero-width joiner in source is invisible and unreviewable.
        final String person = new String(Character.toChars(0x1F468));
        final String seq = person + "\u200D" + person + "\u200D" + person;
        final String text = seq.repeat(4);
        for (int ideal = 1; ideal < text.length(); ideal++) {
            if (Character.isLowSurrogate(text.charAt(ideal))) {
                continue;
            }
            final int result = finder.findChunkEnd(text, 0, ideal, text.length(), 0, 0);
            assertFalse(result < text.length() && text.codePointAt(result) == 0x200D,
                    "boundary must not sit in front of a ZWJ, ideal=" + ideal);
            assertFalse(result > 0 && text.codePointBefore(result) == 0x200D, "boundary must not sit right after a ZWJ, ideal=" + ideal);
        }
    }

    @Test
    public void test_findChunkEnd_hardCut_doesNotSplitVariationSelector() {
        final String text = "\u2764\uFE0F".repeat(20); // HEAVY BLACK HEART + VARIATION SELECTOR-16
        final int result = finder.findChunkEnd(text, 0, 5, text.length(), 0, 0);
        assertFalse(result < text.length() && text.codePointAt(result) == 0xFE0F, "boundary must not sit in front of a variation selector");
    }

    @Test
    public void test_findChunkEnd_clusterAdjust_neverGoesAtOrBeforeStart() {
        // A base character trailed by more combining marks than MAX_CLUSTER_ADJUST can walk back
        // OR forward (30 marks exceeds the 16-code-point budget in both directions), so this
        // hits the aligned give-up path: the result stays at the unmoved ideal offset instead of
        // landing on an unsafe position.
        final String text = "\u304B" + "\u3099".repeat(30);
        final int ideal = 3;
        final int result = finder.findChunkEnd(text, 0, ideal, text.length(), 0, 0);
        assertTrue(result > 0, "must make forward progress, got " + result);
        assertTrue(result <= text.length(), "must not exceed limitEnd, got " + result);
        assertTrue(!finder.clusterContinuation(text.codePointAt(result)) || result == ideal,
                "must be cluster-safe or the unmoved give-up offset, got " + result);
    }

    @Test
    public void test_findChunkEnd_clusterEscape_neverSplitsASurrogatePair() {
        // Regression: the cluster escape used to clamp to upperBound without checking alignment,
        // landing between the two halves of an emoji.
        final String person = new String(Character.toChars(0x1F468));
        final String text = person + "\u200D" + person + "\u200D" + person + person;
        for (final int upperBound : new int[] { 4, 7, text.length() }) {
            final int result = finder.findChunkEnd(text, 0, 2, upperBound, 0, 0);
            assertTrue(result > 0 && result <= upperBound, "upperBound=" + upperBound + " result=" + result);
            assertFalse(
                    result < text.length() && Character.isLowSurrogate(text.charAt(result))
                            && Character.isHighSurrogate(text.charAt(result - 1)),
                    "boundary split a surrogate pair at " + result + " for upperBound=" + upperBound);
        }
    }

    @Test
    public void test_findChunkEnd_clusterEscape_firesWhenBackwardWalkIsBlockedNearStart() {
        // Regression: the escape used to fire only when the backward walk landed exactly on
        // start, so a cluster one offset away still got split.
        final String text = "\u304B" + "\u3099".repeat(3) + "\u304D";
        final int result = finder.findChunkEnd(text, 0, 2, text.length(), 0, 0);
        assertEquals(4, result);
        assertFalse(finder.clusterContinuation(text.codePointAt(result)), "boundary must not sit in front of a combining mark");
    }

    @Test
    public void test_findChunkEnd_negativeStart_doesNotThrow() {
        // start < 0 is out of contract, but a public component method must degrade to an answer
        // rather than an IndexOutOfBounds -- on the cluster-escape path and on the backward-scan
        // path alike.
        final String marks = "\u3099".repeat(3);
        final int viaClusterEscape = finder.findChunkEnd(marks, -1, 1, marks.length(), 0, 0);
        assertTrue(viaClusterEscape >= 0 && viaClusterEscape <= marks.length(), "cluster escape path, got " + viaClusterEscape);
        final String plain = "ab";
        final int viaBackwardScan = finder.findChunkEnd(plain, -1, 1, plain.length(), 1, 0);
        assertTrue(viaBackwardScan >= 0 && viaBackwardScan <= plain.length(), "backward scan path, got " + viaBackwardScan);
    }

    @Test
    public void test_findChunkEnd_alwaysWithinBounds_fuzz() {
        final String text = "Hello world. これはテストです、ok? 東京Tokyo\n\n次の段落。😀👨‍👩 end.";
        for (int start = 0; start < text.length(); start++) {
            for (int size = 2; size <= 12; size++) {
                final int ideal = Math.min(start + size, text.length());
                if (ideal <= start) {
                    continue;
                }
                final int result = finder.findChunkEnd(text, start, ideal, text.length(), 4, 2);
                assertTrue(result > start, "start=" + start + " size=" + size + " result=" + result);
                assertTrue(result <= text.length(), "start=" + start + " size=" + size + " result=" + result);
            }
        }
    }

    // ===================================================================================
    //                                                                      overlap snapping
    //                                                                      ================

    @Test
    public void test_snapOverlapStart_zeroLookback_returnsIdeal() {
        final String text = "alpha beta gamma";
        assertEquals(8, finder.snapOverlapStart(text, 0, 8, 16, 0));
    }

    @Test
    public void test_snapOverlapStart_snapsBackToSpaceBoundary() {
        final String text = "alpha beta gamma";
        // ideal=8 is inside "beta"; the space boundary at 6 is the nearest candidate.
        assertEquals(6, finder.snapOverlapStart(text, 0, 8, 16, 4));
    }

    @Test
    public void test_snapOverlapStart_snapsToJapaneseSentenceBoundary() {
        final String text = "前の文です。次の文です。";
        assertEquals(6, finder.snapOverlapStart(text, 0, 8, 12, 4));
    }

    @Test
    public void test_snapOverlapStart_neverReturnsAtOrBeforeStart() {
        final String text = "a. bbbbbbbbbbbb";
        final int result = finder.snapOverlapStart(text, 3, 6, 12, 10);
        assertTrue(result > 3, "must stay ahead of start, got " + result);
        assertTrue(result <= 12, "must not pass the chunk end, got " + result);
    }

    @Test
    public void test_snapOverlapStart_neverPassesChunkEnd() {
        // No candidate exists; the ideal is returned unchanged and stays within (start, end].
        final String text = "あ".repeat(30);
        final int result = finder.snapOverlapStart(text, 0, 10, 15, 5);
        assertEquals(10, result);
    }

    @Test
    public void test_snapOverlapStart_doesNotSearchForward() {
        // A "。" sits right after ideal; overlap must never move forward (that would drop text).
        final String text = "あいうえお。かきくけこ";
        assertEquals(5, finder.snapOverlapStart(text, 0, 5, 11, 3));
    }

    @Test
    public void test_snapOverlapStart_alwaysWithinBounds_fuzz() {
        final String text = "Hello world. これはテストです、ok? 東京Tokyo\n\n次の段落。😀 end.";
        for (int start = 0; start < text.length() - 2; start++) {
            for (int end = start + 2; end <= Math.min(start + 14, text.length()); end++) {
                for (int ideal = start + 1; ideal <= end; ideal++) {
                    final int result = finder.snapOverlapStart(text, start, ideal, end, 5);
                    assertTrue(result > start, "start=" + start + " ideal=" + ideal + " end=" + end + " result=" + result);
                    assertTrue(result <= end, "start=" + start + " ideal=" + ideal + " end=" + end + " result=" + result);
                }
            }
        }
    }

    @Test
    public void test_snapOverlapStart_outOfContractArguments_degradeToTheChunkEnd() {
        // The method advertises start < result <= end; out-of-contract arguments must degrade to
        // the chunk end rather than handing back an offset the caller has to repair.
        final String text = "alpha beta gamma";
        assertEquals(10, finder.snapOverlapStart(text, 5, 5, 10, 4), "ideal == start");
        assertEquals(10, finder.snapOverlapStart(text, 5, 3, 10, 4), "ideal < start");
        assertEquals(text.length(), finder.snapOverlapStart(text, 0, 999, 999, 4), "ideal past the text");
        assertEquals(5, finder.snapOverlapStart(text, 0, 12, 5, 4), "ideal past the chunk end");
    }

    // ===================================================================================
    //                                                                       subclass seam
    //                                                                       =============

    @Test
    public void test_findChunkEnd_subclassOverridingIsSentenceTerminator_changesBoundarySelection() {
        // Design requirement: subclassing ChunkBoundaryFinder and swapping a protected character
        // predicate must change the boundary the real search picks -- not just change what the
        // predicate itself reports when called directly (that much is already covered by
        // ExposedFinder's delegating accessors below). This pins that findChunkEnd/searchBackward
        // actually dispatch through the protected predicate rather than, say, inlining the switch
        // on '。' into searchBackward -- a refactor that would keep every other test in this class
        // green while silently closing the extension point that makes this class a swappable
        // chunkBoundaryFinder DI component.
        //
        //   text:  あ  い  う  え  お  。  か  き  く  け  こ
        //  index:  0   1   2   3   4   5   6   7   8   9  10
        //
        // ideal=8 (inside "く"), lookback=6 (floor=2), lookahead=0 (no forward search).
        //
        // Base class, by hand: walking back from 8, き(7) and か(6) hit no tier (same HIRAGANA
        // script on both sides, no punctuation). '。'(5) is a sentence terminator -> STRONG,
        // and the candidate for a non-skippable character is the offset right after it, so
        // searchBackward returns candidate=6 immediately (STRONG wins as soon as it is found).
        // findChunkEnd returns that strong candidate unchanged: 6.
        final String text = "あいうえお。かきくけこ";
        final ChunkBoundaryFinder base = new ChunkBoundaryFinder();
        assertEquals(6, base.findChunkEnd(text, 0, 8, text.length(), 6, 0), "base class must treat 。 as a STRONG boundary");

        // Subclass, by hand: with '。' no longer a sentence terminator, it is not a clause
        // separator either (isClauseSeparator does not cover 0x3002) and both neighbours are
        // hiragana (no script change), so it registers in NO tier -- it becomes exactly as
        // boundary-inert as any other hiragana character in this string. Every other character in
        // the window is plain hiragana, so searchBackward finds no candidate at all in either
        // tier. lookahead=0 skips the forward search, and findChunkEnd falls through to the
        // hard-cut path: index 8 sits between two ordinary hiragana code points (not a cluster
        // continuation, no ZWJ before it), so adjustToClusterStart returns it unchanged. Result: 8
        // (the ideal offset, unmoved) -- a different offset than the base class picked for the
        // identical input.
        final ChunkBoundaryFinder noTerminator = new ChunkBoundaryFinder() {
            @Override
            protected boolean isSentenceTerminator(final int cp) {
                return cp != 0x3002 && super.isSentenceTerminator(cp);
            }
        };
        assertEquals(8, noTerminator.findChunkEnd(text, 0, 8, text.length(), 6, 0),
                "a subclass that overrides isSentenceTerminator must see that override honoured inside "
                        + "the real backward search, not just when the predicate is called directly");
    }

    // ===================================================================================
    //                                                    Cluster safety on every tier path
    //                                                    ---------------------------------
    // The escape used to be wired to the hard-cut fallback only, so a STRONG/WEAK/SCRIPT or
    // forward candidate could hand back an offset that split a grapheme cluster -- something the
    // fixed-length cut this search replaces would have left intact. Every test below fails if the
    // guard is removed from its tier.

    @Test
    public void test_findChunkEnd_weakCandidateNeverStrandsACombiningMark() {
        // The space at 2 is a WEAK candidate, but cutting after it strands the combining acute on
        // the far side of the boundary. The cut must fall back to 2 (before the space), never 3.
        final String text = "ab \u0301cd";
        assertEquals(2, finder.findChunkEnd(text, 0, 3, text.length(), 1, 0));
        assertEquals(2, finder.findChunkEnd(text, 0, 3, text.length(), 2, 0));
        assertEquals(2, finder.findChunkEnd(text, 0, 3, text.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_weakCandidateNeverStrandsAVariationSelector() {
        final String text = "ab \uFE0Fcd";
        assertEquals(2, finder.findChunkEnd(text, 0, 3, text.length(), 3, 0));
    }

    @Test
    public void test_findChunkEnd_strongCandidateNeverStrandsACombiningMark() {
        // "ab." is a sentence end with a following space, i.e. a STRONG candidate at 4 -- but 4 is
        // in front of a combining mark, so the STRONG tier must decline it too.
        final String text = "ab. \u0301cd";
        final int result = finder.findChunkEnd(text, 0, 5, text.length(), 5, 0);
        assertTrue(result != 4, "the STRONG tier must not return an offset that strands a combining mark");
        assertFalse(finder.clusterContinuation(Character.codePointAt(text, result)));
    }

    @Test
    public void test_findChunkEnd_forwardSearchNeverStrandsACombiningMark() {
        // The forward search would land on 4, immediately in front of the combining mark.
        final String text = "ab. \u0301cd";
        final int result = finder.findChunkEnd(text, 0, 2, text.length(), 0, 5);
        assertTrue(result != 4, "the forward search must not return an offset that strands a combining mark");
    }

    @Test
    public void test_findChunkEnd_clusterGuardMatchesTheFixedLengthCutItReplaces() {
        // Regression pin: boundary search must never split a cluster the blind cut kept whole.
        final String text = "aaaa bbbb cccc \u0301dddd eeee ffff";
        for (int ideal = 1; ideal < text.length(); ideal++) {
            for (final int lookback : new int[] { 0, 1, 2, 4, 8, 16 }) {
                final int result = finder.findChunkEnd(text, 0, ideal, text.length(), lookback, 0);
                if (result > 0 && result < text.length()) {
                    assertFalse(finder.clusterContinuation(Character.codePointAt(text, result)),
                            "ideal=" + ideal + " lookback=" + lookback + " returned " + result);
                    assertFalse(finder.clusterJoiner(Character.codePointBefore(text, result)),
                            "ideal=" + ideal + " lookback=" + lookback + " returned " + result);
                }
            }
        }
    }

    // ===================================================================================
    //                                                     Punctuation between digits
    //                                                     --------------------------
    // The following-space rule covers ASCII "." "," ";" ":" but cannot cover the fullwidth forms
    // (CJK sentences carry no trailing space) or hyphens. Those require a following non-digit.

    @Test
    public void test_isPunctuationRequiringNonDigit_coversFullwidthFormsAndHyphens() {
        assertTrue(finder.requiringNonDigit('-'));
        assertTrue(finder.requiringNonDigit(0x2010)); // HYPHEN
        assertTrue(finder.requiringNonDigit(0xFF0E)); // FULLWIDTH FULL STOP
        assertTrue(finder.requiringNonDigit(0xFF0C)); // FULLWIDTH COMMA
        assertTrue(finder.requiringNonDigit(0xFF1A)); // FULLWIDTH COLON
        assertFalse(finder.requiringNonDigit('.'));
        assertFalse(finder.requiringNonDigit(0x3002)); // 。
        assertFalse(finder.requiringNonDigit(0x3001)); // 、
        assertFalse(finder.requiringNonDigit(0x2014)); // EM DASH is a real clause separator
    }

    @Test
    public void test_isClauseSeparator_excludesTheNonBreakingHyphen() {
        // U+2011 exists precisely to forbid a break; it must not be a break opportunity.
        assertFalse(finder.clauseSeparator(0x2011));
        assertTrue(finder.clauseSeparator(0x2010));
    }

    @Test
    public void test_findChunkEnd_fullwidthStopBetweenDigitsIsNotABoundary() {
        // 処理速度は従来比で１．５倍に向上しました -- the fullwidth stop is a decimal point here.
        final String text = "\u51E6\u7406\u901F\u5EA6\u306F\u5F93\u6765\u6BD4\u3067\uFF11\uFF0E\uFF15\u500D"
                + "\u306B\u5411\u4E0A\u3057\u307E\u3057\u305F";
        final int result = finder.findChunkEnd(text, 0, 12, text.length(), 12, 0);
        assertTrue(result != 11, "a fullwidth stop between fullwidth digits must not end a chunk");
    }

    @Test
    public void test_findChunkEnd_fullwidthCommaAndColonBetweenDigitsAreNotBoundaries() {
        // 売上高は１，２３４，５６７円です
        final String comma = "\u58F2\u4E0A\u9AD8\u306F\uFF11\uFF0C\uFF12\uFF13\uFF14\uFF0C\uFF15\uFF16\uFF17" + "\u5186\u3067\u3059";
        assertTrue(finder.findChunkEnd(comma, 0, 10, comma.length(), 10, 0) != 6,
                "a fullwidth comma between fullwidth digits must not end a chunk");
        // 開始は１０：３０からです
        final String colon = "\u958B\u59CB\u306F\uFF11\uFF10\uFF1A\uFF13\uFF10\u304B\u3089\u3067\u3059";
        assertTrue(finder.findChunkEnd(colon, 0, 7, colon.length(), 7, 0) != 6,
                "a fullwidth colon between fullwidth digits must not end a chunk");
    }

    @Test
    public void test_findChunkEnd_fullwidthStopIsStillASentenceEndBeforeANonDigit() {
        // The JIS 、。-替わりの ，． convention must keep working: これで終わり．次は導入手順です
        final String text = "\u3053\u308C\u3067\u7D42\u308F\u308A\uFF0E\u6B21\u306F\u5C0E\u5165\u624B\u9806" + "\u3067\u3059";
        assertEquals(7, finder.findChunkEnd(text, 0, 10, text.length(), 10, 0));
    }

    @Test
    public void test_findChunkEnd_hyphenBetweenDigitsIsNotABoundary() {
        // Both hyphens sit between digits, so the cut falls back to the space in front of the date
        // (12) instead of landing at 17 or 20.
        final String date = "released on 2026-08-09 in tokyo";
        assertEquals(12, finder.findChunkEnd(date, 0, 19, date.length(), 20, 0));
        final String version = "charset is UTF-8 today please";
        assertEquals(11, finder.findChunkEnd(version, 0, 15, version.length(), 8, 0));
    }

    @Test
    public void test_findChunkEnd_hyphenBeforeALetterStillBreaks() {
        // Breaking after the hyphen of a hyphenated compound is ordinary line-break behaviour and
        // is deliberately kept.
        final String text = "send an e-mail today please";
        assertEquals(10, finder.findChunkEnd(text, 0, 14, text.length(), 6, 0));
    }

    @Test
    public void test_findChunkEnd_asciiDecimalsAreStillProtectedByTheSpaceRule() {
        // Guard that the new digit rule did not displace the pre-existing following-space rule.
        assertEquals(5, finder.findChunkEnd("aaaa 3.1415926 bbbb", 0, 8, 19, 8, 0));
        assertEquals(5, finder.findChunkEnd("aaaa 1,234,567 bbbb", 0, 8, 19, 8, 0));
    }

    @Test
    public void test_isPunctuationRequiringNonDigit_isDispatchedThroughTheSearch() {
        // The predicate is part of the documented subclass seam, so the search must call it
        // virtually rather than inlining its character set.
        final ChunkBoundaryFinder allowsDigits = new ChunkBoundaryFinder() {
            @Override
            protected boolean isPunctuationRequiringNonDigit(final int cp) {
                return false;
            }
        };
        final String version = "charset is UTF-8 today please";
        assertEquals(11, finder.findChunkEnd(version, 0, 15, version.length(), 8, 0));
        assertEquals(15, allowsDigits.findChunkEnd(version, 0, 15, version.length(), 8, 0),
                "a subclass that opts out of the digit rule must see the hyphen accepted again");
    }

    @Test
    public void test_findChunkEnd_propertySweep_clusterSafeAlignedAndMonotonic() {
        // Randomised sweep over a pool built to hit every tier plus the shapes that used to break
        // the invariants: combining marks, ZWJ, variation selectors, astral code points, fullwidth
        // punctuation and digits. Fixed seed, so a failure is reproducible.
        final int[] pool = { 'a', 'b', ' ', '.', ',', '-', '\'', 0x0301, 0x200D, 0xFE0F, 0x3042, 0x65E5, 'A', 0x2029, 0x300C, 0xFF0E,
                0xFF0C, 0xFF10, 0x1F600 };
        final java.util.Random random = new java.util.Random(20260809L);
        for (int iteration = 0; iteration < 20000; iteration++) {
            final StringBuilder builder = new StringBuilder();
            final int codePoints = 4 + random.nextInt(12);
            for (int i = 0; i < codePoints; i++) {
                builder.appendCodePoint(pool[random.nextInt(pool.length)]);
            }
            final String text = builder.toString();
            final int ideal = 1 + random.nextInt(Math.max(1, text.length() - 1));
            final int lookahead = random.nextInt(4);
            int previous = Integer.MAX_VALUE;
            for (int lookback = 0; lookback <= 8; lookback++) {
                final int result = finder.findChunkEnd(text, 0, ideal, text.length(), lookback, lookahead);
                final String where = "text="
                        + text.codePoints().mapToObj(cp -> String.format("U+%04X", cp)).collect(java.util.stream.Collectors.joining(" "))
                        + " ideal=" + ideal + " lookback=" + lookback + " lookahead=" + lookahead + " result=" + result;
                assertTrue(result > 0 && result <= text.length(), where);
                assertFalse(Character.isLowSurrogate(text.charAt(Math.min(result, text.length() - 1))) && result > 0
                        && result < text.length() && Character.isHighSurrogate(text.charAt(result - 1)),
                        "a surrogate pair must never be split: " + where);
                if (result < text.length()) {
                    assertFalse(finder.clusterContinuation(Character.codePointAt(text, result)),
                            "no tier may strand a cluster continuation: " + where);
                    assertFalse(finder.clusterJoiner(Character.codePointBefore(text, result)),
                            "no tier may cut right after a joiner: " + where);
                }
                if (lookback > 0) {
                    assertTrue(result <= previous, "widening lookback must not move the boundary later: " + where);
                }
                previous = result;
            }
        }
    }

    /** Exposes the protected character predicates for assertion. */
    private static final class ExposedFinder extends ChunkBoundaryFinder {
        boolean breakableSpace(final int cp) {
            return isBreakableSpace(cp);
        }

        boolean newline(final int cp) {
            return isNewline(cp);
        }

        boolean sentenceTerminator(final int cp) {
            return isSentenceTerminator(cp);
        }

        boolean clauseSeparator(final int cp) {
            return isClauseSeparator(cp);
        }

        boolean asciiRequiringSpace(final int cp) {
            return isAsciiPunctuationRequiringSpace(cp);
        }

        boolean closingBracket(final int cp) {
            return isClosingBracket(cp);
        }

        boolean openingBracket(final int cp) {
            return isOpeningBracket(cp);
        }

        boolean skippable(final int cp) {
            return isSkippableAfterBoundary(cp);
        }

        boolean scriptBoundary(final int before, final int after) {
            return isScriptBoundary(before, after);
        }

        boolean clusterContinuation(final int cp) {
            return isClusterContinuation(cp);
        }

        boolean clusterJoiner(final int cp) {
            return isClusterJoiner(cp);
        }

        boolean requiringNonDigit(final int cp) {
            return isPunctuationRequiringNonDigit(cp);
        }
    }
}
